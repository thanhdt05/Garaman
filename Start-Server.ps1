param(
    [switch]$Build = $false
)
$ErrorActionPreference = 'Stop'
$repoRoot = $PSScriptRoot
$tomcatDir = 'E:\java\apache-tomcat-9.0.86'
$warSource = Join-Path $repoRoot 'app\target\garaman.war'

if ($Build -or (-not (Test-Path $warSource))) {
    Write-Host "Dang build file WAR..." -ForegroundColor Cyan
    & "E:\java\apache-maven-3.9.6\bin\mvn.cmd" -f (Join-Path $repoRoot 'app\pom.xml') package -DskipTests
}

Write-Host "Deploying garaman.war vao Tomcat 9..." -ForegroundColor Cyan
Copy-Item $warSource (Join-Path $tomcatDir 'webapps\garaman.war') -Force

Write-Host "Dang khoi dong Apache Tomcat 9..." -ForegroundColor Green
$env:CATALINA_HOME = $tomcatDir
$env:CATALINA_BASE = $tomcatDir
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-25.0.2'
$env:JAVA_OPTS = '-Dfile.encoding=UTF-8'

$batFile = Join-Path $repoRoot 'run-tomcat.bat'
Start-Process -FilePath "cmd.exe" -ArgumentList "/c `"$batFile`"" -WorkingDirectory $repoRoot

Start-Sleep -Seconds 4
Write-Host "`n=======================================================" -ForegroundColor Green
Write-Host " GaraMan Backend da khoi dong thanh cong!" -ForegroundColor Green
Write-Host " URL dang nhap: http://localhost:8080/garaman/login" -ForegroundColor Yellow
Write-Host " Tai khoan test:" -ForegroundColor White
Write-Host "   - Khach hang:    khach26 / 123456" -ForegroundColor Gray
Write-Host "   - Nhan vien kho: kho26   / 123456" -ForegroundColor Gray
Write-Host "=======================================================`n" -ForegroundColor Green
Write-Host "De tat server, ban chi can dong cua so Tomcat hoac chay: .\Stop-Server.ps1" -ForegroundColor Cyan

Start-Process "http://localhost:8080/garaman/login"
