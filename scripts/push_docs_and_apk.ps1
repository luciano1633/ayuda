# Script para copiar APK y preparar commit de documentación
# Uso: abrir PowerShell en la raíz del repo y ejecutar:
# .\scripts\push_docs_and_apk.ps1

$apkPath = "app\build\outputs\apk\debug\app-debug.apk"
$dest = "docs\mocks\app-debug.apk"

if (Test-Path $apkPath) {
    Copy-Item -Path $apkPath -Destination $dest -Force
    Write-Host "APK copiado a $dest"
} else {
    Write-Host "APK no encontrado en $apkPath. Ejecuta .\gradlew.bat :app:assembleDebug primero." -ForegroundColor Yellow
}

Write-Host "Archivos preparados en docs/. Puedes hacer: git add docs/; git commit -m 'docs: add UI docs and apk'; git push"
