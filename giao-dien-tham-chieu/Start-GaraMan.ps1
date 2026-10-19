param(
    [string]$TomcatHome = '',
    [ValidateRange(1024, 65535)][int]$Port = 8080
)
$ErrorActionPreference = 'Stop'
$projectDirectory = $PSScriptRoot
if (-not $TomcatHome) {
    $bundledTomcat = Get-ChildItem -LiteralPath (Join-Path $projectDirectory '.tools') -Directory -Filter 'apache-tomcat-*' -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1
    if ($bundledTomcat) { $TomcatHome = $bundledTomcat.FullName }
    elseif ($env:CATALINA_HOME) { $TomcatHome = $env:CATALINA_HOME }
    else { throw 'Chua tim thay Tomcat. Chay: .\Start-GaraMan.ps1 -TomcatHome "C:\duong-dan\apache-tomcat"' }
}
$tomcatDirectory = (Resolve-Path -LiteralPath $TomcatHome).Path
if (-not (Test-Path -LiteralPath (Join-Path $tomcatDirectory 'bin\bootstrap.jar'))) { throw 'Thu muc Tomcat khong hop le.' }
$javaCommand = Get-Command java.exe -ErrorAction Stop
$runtimeDirectory = Join-Path $projectDirectory '.runtime\tomcat'
$pidFile = Join-Path $projectDirectory '.runtime\tomcat.pid'
if (Test-Path -LiteralPath $pidFile) {
    $existingId = [int](Get-Content -LiteralPath $pidFile)
    $existing = Get-CimInstance Win32_Process -Filter "ProcessId = $existingId" -ErrorAction SilentlyContinue
    if ($existing -and $existing.CommandLine -like "*catalina.base=$runtimeDirectory*") {
        Write-Host 'GaraMan dang chay. Dung Stop-GaraMan.ps1 truoc khi khoi dong lai.'
        exit 0
    }
}
if (Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue) { throw "Cong $Port dang duoc su dung. Thu -Port 8081." }
foreach ($directory in @('conf','logs','temp','work','webapps')) {
    New-Item -ItemType Directory -Path (Join-Path $runtimeDirectory $directory) -Force | Out-Null
}
Copy-Item -Path (Join-Path $tomcatDirectory 'conf\*') -Destination (Join-Path $runtimeDirectory 'conf') -Force
$webDirectory = [System.Security.SecurityElement]::Escape((Join-Path $projectDirectory 'web'))
$serverXml = @"
<?xml version="1.0" encoding="UTF-8"?>
<Server port="-1">
  <Service name="Catalina">
    <Connector address="127.0.0.1" port="$Port" protocol="HTTP/1.1" connectionTimeout="20000" URIEncoding="UTF-8" />
    <Engine name="Catalina" defaultHost="localhost">
      <Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="false">
        <Context path="/GaraMan" docBase="$webDirectory" reloadable="false" />
      </Host>
    </Engine>
  </Service>
</Server>
"@
[System.IO.File]::WriteAllText((Join-Path $runtimeDirectory 'conf\server.xml'), $serverXml, [System.Text.UTF8Encoding]::new($false))
$classPath = (Join-Path $tomcatDirectory 'bin\bootstrap.jar') + ';' + (Join-Path $tomcatDirectory 'bin\tomcat-juli.jar')
$arguments = @(
    "`"-Dcatalina.home=$tomcatDirectory`"",
    "`"-Dcatalina.base=$runtimeDirectory`"",
    "`"-Djava.io.tmpdir=$(Join-Path $runtimeDirectory 'temp')`"",
    '-Dfile.encoding=UTF-8',
    '-classpath', "`"$classPath`"",
    'org.apache.catalina.startup.Bootstrap', 'start'
)
$process = Start-Process -FilePath $javaCommand.Source -ArgumentList $arguments -WorkingDirectory $projectDirectory -WindowStyle Hidden -RedirectStandardOutput (Join-Path $runtimeDirectory 'logs\stdout.log') -RedirectStandardError (Join-Path $runtimeDirectory 'logs\stderr.log') -PassThru
Set-Content -LiteralPath $pidFile -Value $process.Id -Encoding ascii
Write-Host "GaraMan: http://localhost:$Port/GaraMan/"
Write-Host "Nhat ky: $runtimeDirectory\logs"
Write-Host 'Dung may chu: .\Stop-GaraMan.ps1'
