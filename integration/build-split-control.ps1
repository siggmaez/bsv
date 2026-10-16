param(
    [string]$Sdk = "$PSScriptRoot\..\.tooling\android-sdk",
    [string]$InputApks = "$PSScriptRoot\..\.tooling\brawlstars-original",
    [string]$EditorJar = "$PSScriptRoot\..\.tooling\APKEditor.jar"
)
# Diagnostic control: retains original split layout and adds NO BSV code.
$ErrorActionPreference = 'Stop'
$workspace = (Resolve-Path "$PSScriptRoot\..").Path
$work = Join-Path $workspace '.tooling\split-control-build'
$output = Join-Path $workspace '.tooling\split-control-apks'
New-Item -ItemType Directory -Path "$work\tools", $output -Force | Out-Null
function Check-Exit { if ($LASTEXITCODE -ne 0) { throw "Tool failed: $LASTEXITCODE" } }
& javac -encoding UTF-8 -cp $EditorJar -d "$work\tools" "$PSScriptRoot\tools\PatchManifest.java"
Check-Exit
$tools = Join-Path $Sdk 'build-tools\35.0.0'
foreach ($apk in (Get-ChildItem -LiteralPath $InputApks -Filter '*.apk' | Sort-Object Name)) {
    Write-Output "Packaging control: $($apk.Name)"
    & java -Xmx4G -cp "$EditorJar;$work\tools" PatchManifest $apk.FullName "$work\unsigned.apk" '-'
    Check-Exit
    & "$tools\zipalign.exe" -P 16 -f 4 "$work\unsigned.apk" "$work\aligned.apk"
    Check-Exit
    & "$tools\apksigner.bat" sign --ks "$env:USERPROFILE\.android\debug.keystore" --ks-key-alias androiddebugkey --ks-pass pass:android --key-pass pass:android --out "$output\$($apk.Name)" "$work\aligned.apk"
    Check-Exit
    & "$tools\apksigner.bat" verify "$output\$($apk.Name)"
    Check-Exit
}
Write-Output "Control APKs: $output"
