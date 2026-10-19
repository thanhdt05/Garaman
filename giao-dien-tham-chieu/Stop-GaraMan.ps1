$ErrorActionPreference = 'Stop'
$runtimeDirectory = Join-Path $PSScriptRoot '.runtime\tomcat'
$pidFile = Join-Path $PSScriptRoot '.runtime\tomcat.pid'
if (-not (Test-Path -LiteralPath $pidFile)) { Write-Host 'Khong co may chu GaraMan do script khoi dong.'; exit 0 }
$serverProcessId = [int](Get-Content -LiteralPath $pidFile)
$serverProcess = Get-CimInstance Win32_Process -Filter "ProcessId = $serverProcessId" -ErrorAction SilentlyContinue
if (-not $serverProcess) { Write-Host 'May chu da dung.'; exit 0 }
if ($serverProcess.Name -ne 'java.exe' -or $serverProcess.CommandLine -notlike "*catalina.base=$runtimeDirectory*") {
    throw 'PID khong thuoc may chu GaraMan. Khong dung tien trinh nay.'
}
Stop-Process -Id $serverProcessId
Write-Host 'Da dung may chu GaraMan.'
