package dao;

import model.SparePart4;
import model.SparePartCategory4;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SparePartDAO4 extends DAO {

    public List<SparePart4> findSparePartsByName(String keyword, boolean publishedOnly) throws SQLException {
        List<SparePart4> list = new ArrayList<>();
        String sql;
        if (publishedOnly) {
            sql = "SELECT p.id, p.name, p.tblSparePartCategory4id, p.description, p.imagePath, p.unit,        p.manufacturer, p.specifications, p.compatibleVehicleModels, p.sellingPrice, p.warrantyPeriod,        p.warrantyTerms, p.status, p.stockQuantity,        c.name AS categoryName, c.description AS categoryDescription, c.status AS categoryStatus FROM tblSparePart4 p JOIN tblSparePartCategory4 c ON p.tblSparePartCategory4id = c.id WHERE p.status = ? AND p.name LIKE ? ORDER BY p.name ASC";
        } else {
            sql = "SELECT p.id, p.name, p.tblSparePartCategory4id, p.description, p.imagePath, p.unit,        p.manufacturer, p.specifications, p.compatibleVehicleModels, p.sellingPrice, p.warrantyPeriod,        p.warrantyTerms, p.status, p.stockQuantity,        c.name AS categoryName, c.description AS categoryDescription, c.status AS categoryStatus FROM tblSparePart4 p JOIN tblSparePartCategory4 c ON p.tblSparePartCategory4id = c.id WHERE p.status IN (?, ?) AND p.name LIKE ? ORDER BY p.name ASC";
        }

        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            if (publishedOnly) {
                ps.setString(1, "Đang kinh doanh");
                ps.setString(2, createSearchPattern(keyword));
            } else {
                ps.setString(1, "Chưa công bố");
                ps.setString(2, "Đang kinh doanh");
                ps.setString(3, createSearchPattern(keyword));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSparePart(rs));
                }
            }
        }
        return list;
    }

    public SparePart4 getSparePartDetails(int sparePartId, boolean publishedOnly) throws SQLException {
        if (sparePartId <= 0) {
            return null;
        }

        String sql;
        if (publishedOnly) {
            sql = "SELECT p.id, p.name, p.tblSparePartCategory4id, p.description, p.imagePath, p.unit,        p.manufacturer, p.specifications, p.compatibleVehicleModels, p.sellingPrice, p.warrantyPeriod,        p.warrantyTerms, p.status, p.stockQuantity,        c.name AS categoryName, c.description AS categoryDescription, c.status AS categoryStatus FROM tblSparePart4 p JOIN tblSparePartCategory4 c ON p.tblSparePartCategory4id = c.id WHERE p.id = ? AND p.status = ?";
        } else {
            sql = "SELECT p.id, p.name, p.tblSparePartCategory4id, p.description, p.imagePath, p.unit,        p.manufacturer, p.specifications, p.compatibleVehicleModels, p.sellingPrice, p.warrantyPeriod,        p.warrantyTerms, p.status, p.stockQuantity,        c.name AS categoryName, c.description AS categoryDescription, c.status AS categoryStatus FROM tblSparePart4 p JOIN tblSparePartCategory4 c ON p.tblSparePartCategory4id = c.id WHERE p.id = ? AND p.status IN (?, ?)";
        }

        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, sparePartId);
            if (publishedOnly) {
                ps.setString(2, "Đang kinh doanh");
            } else {
                ps.setString(2, "Chưa công bố");
                ps.setString(3, "Đang kinh doanh");
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSparePart(rs);
                }
            }
        }
        return null;
    }

    public BigDecimal getStockQuantity(int sparePartId) throws SQLException {
        if (sparePartId <= 0) {
            return BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
        }

        String sql = "SELECT stockQuantity FROM tblSparePart4 WHERE id = ?";
        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, sparePartId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal stock = rs.getBigDecimal("stockQuantity");
                    return stock != null ? stock.setScale(3, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
                }
            }
        }
        return BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
    }

    /**
     * Tăng số lượng tồn kho của phụ tùng trong phạm vi transaction của hóa đơn nhập.
     * Phương thức có phạm vi package-private, nhận connection ngoài và KHÔNG tự commit hay đóng.
     */
    void increaseStockAfterPurchase(Connection connection, int sparePartId, BigDecimal quantity)
    throws SQLException, IllegalArgumentException {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("quantity: Số lượng cộng tồn phải lớn hơn 0");
        }

        String sql = "UPDATE tblSparePart4 SET stockQuantity = stockQuantity + ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBigDecimal(1, quantity);
            ps.setInt(2, sparePartId);
            int rows = ps.executeUpdate();
            if (rows != 1) {
                throw new SQLException("Không thể cập nhật tồn kho cho phụ tùng ID: " + sparePartId);
            }
        }
    }

    public int addSparePart(SparePart4 sparePart) throws SQLException, IllegalArgumentException {
        {
            if (sparePart == null) {
                throw new IllegalArgumentException("sparePart: Thông tin phụ tùng không được null");
            }
            {
                if (sparePart.getName() == null) {
                    throw new IllegalArgumentException("name: Trường này là bắt buộc");
                }
                String trimmed = sparePart.getName().trim();
                if (trimmed.isEmpty()) {
                    throw new IllegalArgumentException("name: Trường này không được để trống");
                }
                if (trimmed.length() > 255) {
                    throw new IllegalArgumentException("name: Độ dài không được vượt quá " + 255 + " ký tự");
                }
            }
            {
                if (sparePart.getManufacturer() == null) {
                    throw new IllegalArgumentException("manufacturer: Trường này là bắt buộc");
                }
                String trimmed = sparePart.getManufacturer().trim();
                if (trimmed.isEmpty()) {
                    throw new IllegalArgumentException("manufacturer: Trường này không được để trống");
                }
                if (trimmed.length() > 255) {
                    throw new IllegalArgumentException("manufacturer: Độ dài không được vượt quá " + 255 + " ký tự");
                }
            }

            String unit = sparePart.getUnit();
            if (unit == null || (!"Cái".equals(unit) &&
            !"Chiếc".equals(unit) &&
            !"Bộ".equals(unit) &&
            !"Lít".equals(unit))) {
                throw new IllegalArgumentException("unit: Đơn vị tính không hợp lệ (chỉ chấp nhận Cái, Chiếc, Bộ, Lít)");
            }

            if (sparePart.getCategory() == null || sparePart.getCategory().getId() <= 0) {
                throw new IllegalArgumentException("categoryId: Phải chọn nhóm phụ tùng hợp lệ đang sử dụng");
            }

            String status = sparePart.getStatus();
            if (status == null || (!"Chưa công bố".equals(status) &&
            !"Đang kinh doanh".equals(status))) {
                throw new IllegalArgumentException("status: Trạng thái phụ tùng mới chỉ được chọn 'Chưa công bố' hoặc 'Đang kinh doanh'");
            }

            if ("Đang kinh doanh".equals(status)) {
                if (sparePart.getSellingPrice() == null || sparePart.getSellingPrice().compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("sellingPrice: Phụ tùng đang kinh doanh bắt buộc phải có giá bán >= 0");
                }
                if (sparePart.getDescription() == null || sparePart.getDescription().trim().isEmpty()) {
                    throw new IllegalArgumentException("description: Phụ tùng đang kinh doanh bắt buộc phải có mô tả");
                }
            }

            if (sparePart.getImagePath() != null && !sparePart.getImagePath().trim().isEmpty()) {
                String img = sparePart.getImagePath().trim();
                if (!"assets/images/part.svg".equals(img) &&
                !"assets/images/brake.svg".equals(img) &&
                !"assets/images/placeholder.svg".equals(img)) {
                    throw new IllegalArgumentException("imagePath: Đường dẫn ảnh minh họa không nằm trong danh sách tài nguyên cục bộ hợp lệ");
                }
            }
        }

        String sql = "INSERT INTO tblSparePart4  (name, tblSparePartCategory4id, description, imagePath, unit, manufacturer,   specifications, compatibleVehicleModels, sellingPrice, warrantyPeriod, warrantyTerms, status, stockQuantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, sparePart.getName().trim());
            ps.setInt(2, sparePart.getCategory().getId());
            ps.setString(3, sparePart.getDescription());
            ps.setString(4, sparePart.getImagePath());
            ps.setString(5, sparePart.getUnit());
            ps.setString(6, sparePart.getManufacturer().trim());
            ps.setString(7, sparePart.getSpecifications());
            ps.setString(8, sparePart.getCompatibleVehicleModels());

            if (sparePart.getSellingPrice() != null) {
                ps.setBigDecimal(9, sparePart.getSellingPrice());
            } else {
                ps.setNull(9, java.sql.Types.DECIMAL);
            }

            ps.setString(10, sparePart.getWarrantyPeriod());
            ps.setString(11, sparePart.getWarrantyTerms());
            ps.setString(12, sparePart.getStatus());
            ps.setBigDecimal(13, BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP));

            int affected = ps.executeUpdate();
            if (affected != 1) {
                throw new SQLException("Thêm phụ tùng thất bại");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    sparePart.setId(id);
                    return id;
                } else {
                    throw new SQLException("Thêm phụ tùng thành công nhưng không lấy được ID tự sinh");
                }
            }
        }
    }

    private SparePart4 mapSparePart(ResultSet rs) throws SQLException {
        SparePart4 p = new SparePart4();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setImagePath(rs.getString("imagePath"));
        p.setUnit(rs.getString("unit"));
        p.setManufacturer(rs.getString("manufacturer"));
        p.setSpecifications(rs.getString("specifications"));
        p.setCompatibleVehicleModels(rs.getString("compatibleVehicleModels"));

        BigDecimal sellingPrice = rs.getBigDecimal("sellingPrice");
        p.setSellingPrice(sellingPrice);

        p.setWarrantyPeriod(rs.getString("warrantyPeriod"));
        p.setWarrantyTerms(rs.getString("warrantyTerms"));
        p.setStatus(rs.getString("status"));

        BigDecimal stockQuantity = rs.getBigDecimal("stockQuantity");
        p.setStockQuantity(stockQuantity != null ? stockQuantity.setScale(3, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP));

        SparePartCategory4 cat = new SparePartCategory4();
        cat.setId(rs.getInt("tblSparePartCategory4id"));
        cat.setName(rs.getString("categoryName"));
        cat.setDescription(rs.getString("categoryDescription"));
        cat.setStatus(rs.getString("categoryStatus"));
        p.setCategory(cat);

        return p;
    }
}
