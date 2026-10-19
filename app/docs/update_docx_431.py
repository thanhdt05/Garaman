import zipfile
import re
import os
import xml.sax.saxutils as saxutils

def make_p(text, bold=False, italic=False, is_heading=False, is_bullet=False):
    escaped = saxutils.escape(text)
    sz = "26" if not is_heading else "26"
    b_tag = "<w:b/><w:bCs/>" if bold or is_heading else ""
    i_tag = "<w:i/><w:iCs/>" if italic else ""
    spacing_before = "140" if is_heading else "0"
    spacing_after = "80" if is_heading else "60"
    
    bullet_prefix = ""
    if is_bullet:
        bullet_prefix = '<w:pPr><w:pStyle w:val="Normal"/><w:spacing w:lineRule="auto" w:line="276" w:before="0" w:after="60"/><w:ind w:left="400"/><w:jc w:val="both"/><w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman"/><w:color w:val="000000"/><w:sz w:val="26"/><w:szCs w:val="26"/></w:rPr></w:pPr>'
    else:
        bullet_prefix = f'<w:pPr><w:pStyle w:val="Normal"/><w:spacing w:lineRule="auto" w:line="276" w:before="{spacing_before}" w:after="{spacing_after}"/><w:jc w:val="both"/><w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman"/>{b_tag}{i_tag}<w:color w:val="000000"/><w:sz w:val="{sz}"/><w:szCs w:val="{sz}"/></w:rPr></w:pPr>'
        
    return f'<w:p>{bullet_prefix}<w:r><w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman"/>{b_tag}{i_tag}<w:color w:val="000000"/><w:sz w:val="{sz}"/><w:szCs w:val="{sz}"/></w:rPr><w:t xml:space="preserve">{escaped}</w:t></w:r></w:p>'

def parse_md_to_xml(md_text):
    xml_paragraphs = []
    lines = md_text.splitlines()
    for line in lines:
        line = line.strip()
        if not line:
            continue
        if line.startswith("#### "):
            # Sub-heading (e.g. Bước 1. Thiết kế giao diện)
            xml_paragraphs.append(make_p(line[5:], bold=True, is_heading=True))
        elif line.startswith("### "):
            # Heading 3
            xml_paragraphs.append(make_p(line[4:], bold=True, is_heading=True))
        elif line.startswith("- **") and "**" in line[4:]:
            # Bullet with bold lead
            xml_paragraphs.append(make_p(line[2:], is_bullet=True))
        elif line.startswith("- "):
            xml_paragraphs.append(make_p(line[2:], is_bullet=True))
        elif line.startswith("|") and not line.startswith("|---"):
            # Table row as formatted text
            cells = [c.strip() for c in line.split("|")[1:-1]]
            row_text = " | ".join(cells)
            xml_paragraphs.append(make_p(row_text, is_bullet=True))
        elif line.startswith("*") and line.endswith("*"):
            xml_paragraphs.append(make_p(line[1:-1], italic=True))
        else:
            xml_paragraphs.append(make_p(line))
    return "".join(xml_paragraphs)

def main():
    docx_path = "app/docs/BAO_CAO_DA_SUA.docx"
    with open("app/docs/4.3.1_THIET_KE_TINH.md", "r", encoding="utf-8") as f:
        md_content = f.read()

    # Tách phần intro, phần a và phần b
    part_intro = ""
    part_a = ""
    part_b = ""

    if "### a." in md_content and "### b." in md_content:
        idx_a = md_content.find("### a.")
        idx_b = md_content.find("### b.")
        part_intro = md_content[:idx_a].strip()
        part_a = md_content[idx_a:idx_b].replace("### a. Thiết kế tĩnh modul khách hàng tìm kiếm thông tin dịch vụ/phụ tùng", "").strip()
        part_b = md_content[idx_b:].replace("### b. Thiết kế tĩnh modul nhân viên kho nhập phụ tùng", "").strip()

    xml_intro = parse_md_to_xml(part_intro)
    xml_a = parse_md_to_xml(part_a)
    xml_b = parse_md_to_xml(part_b)

    # Đọc zip document.xml
    with zipfile.ZipFile(docx_path, "r") as z:
        doc_xml = z.read("word/document.xml").decode("utf-8")

    # Tìm vị trí tiêu đề a và b
    pos_431 = doc_xml.find("4.3.1 Thiết kế tĩnh")
    if pos_431 == -1:
        print("Lỗi: Không tìm thấy 4.3.1")
        return

    # Tìm paragraph a
    pos_a = doc_xml.find("a. Thiết kế tĩnh modul khách hàng tìm kiếm thông tin", pos_431)
    end_p_a = doc_xml.find("</w:p>", pos_a) + 6

    # Tìm paragraph b
    pos_b = doc_xml.find("b. Thiết kế tĩnh modul nhân viên kho nhập phụ tùng", end_p_a)
    end_p_b = doc_xml.find("</w:p>", pos_b) + 6

    # Tìm thẻ đóng body
    pos_body_end = doc_xml.find("</w:body>")

    # Tái cấu trúc xml:
    # 1. Trước paragraph a: chèn intro vào trước a (hoặc ngay sau 4.3.1)
    # 2. Sau paragraph a: chèn xml_a
    # 3. Sau paragraph b: chèn xml_b
    
    # Đoạn giữa pos_431 và pos_a
    before_a = doc_xml[:pos_a]
    p_a_tag_start = doc_xml.rfind("<w:p>", 0, pos_a)
    
    new_xml = (
        doc_xml[:p_a_tag_start]
        + xml_intro
        + doc_xml[p_a_tag_start:end_p_a]
        + xml_a
        + doc_xml[end_p_a:end_p_b]
        + xml_b
        + doc_xml[end_p_b:]
    )

    # Ghi lại file zip
    temp_docx = "app/docs/BAO_CAO_DA_SUA_TEMP.docx"
    with zipfile.ZipFile(docx_path, "r") as zin:
        with zipfile.ZipFile(temp_docx, "w", compression=zipfile.ZIP_DEFLATED) as zout:
            for item in zin.infolist():
                if item.filename == "word/document.xml":
                    zout.writestr(item, new_xml.encode("utf-8"))
                else:
                    zout.writestr(item, zin.read(item.filename))

    os.replace(temp_docx, docx_path)
    print("Cập nhật thành công mục 4.3.1 vào BAO_CAO_DA_SUA.docx!")

if __name__ == "__main__":
    main()
