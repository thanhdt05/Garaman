$ErrorActionPreference = 'Stop'
$tomcatDir = 'E:\java\apache-tomcat-9.0.86'
$env:CATALINA_HOME = $tomcatDir
$env:CATALINA_BASE = $tomcatDir
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-25.0.2'
$shutdownBat = Join-Path $tomcatDir 'bin\shutdown.bat'
if (Test-Path $shutdownBat) {
    Write-Host "Dang tat Apache Tomcat 9..." -ForegroundColor Yellow
    Start-Process -FilePath "cmd.exe" -ArgumentList "/c `"$shutdownBat`"" -Wait -WindowStyle Hidden -WorkingDirectory (Join-Path $tomcatDir 'bin')
    Write-Host "Tomcat da duoc tat thanh cong!" -ForegroundColor Green
}
