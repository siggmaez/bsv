param(
    [string]$Sdk = "$PSScriptRoot\..\.tooling\android-sdk",
    [string]$InputApks = "$PSScriptRoot\..\.tooling\brawlstars-original",
    [string]$EditorJar = "$PSScriptRoot\..\.tooling\APKEditor.jar"
)
$ErrorActionPreference = 'Stop'
$workspace = (Resolve-Path "$PSScriptRoot\..").Path
$work = Join-Path $workspace '.tooling\client-build'
New-Item -ItemType Directory -Path "$work\classes", "$work\tools", "$work\dex", "$workspace\dist" -Force | Out-Null
function Check-Exit { if ($LASTEXITCODE -ne 0) { throw "Tool failed: $LASTEXITCODE" } }
$androidJar = Join-Path $Sdk 'platforms\android-35\android.jar'
$tools = Join-Path $Sdk 'build-tools\35.0.0'
$sources = Get-ChildItem "$PSScriptRoot\src" -Filter '*.java' -Recurse | ForEach-Object FullName
& javac -encoding UTF-8 -source 8 -target 8 -cp $androidJar -d "$work\classes" @sources
Check-Exit
& jar cf "$work\menu.jar" -C "$work\classes" .
Check-Exit
& "$tools\d8.bat" --lib $androidJar --min-api 26 --output "$work\dex" "$work\menu.jar"
Check-Exit
& javac -encoding UTF-8 -cp $EditorJar -d "$work\tools" "$PSScriptRoot\tools\PatchManifest.java"
Check-Exit
& java -Xmx4G -jar $EditorJar m -i $InputApks -o "$work\merged.apk" -clean-meta -validate-modules
Check-Exit
& java -Xmx4G -cp "$EditorJar;$work\tools" PatchManifest "$work\merged.apk" "$work\unsigned.apk" "$work\dex\classes.dex"
Check-Exit
& "$tools\zipalign.exe" -P 16 -f 4 "$work\unsigned.apk" "$work\aligned.apk"
Check-Exit
$output = "$workspace\dist\BSV-client-69.414-smoke-debug.apk"
& "$tools\apksigner.bat" sign --ks "$env:USERPROFILE\.android\debug.keystore" --ks-key-alias androiddebugkey --ks-pass pass:android --key-pass pass:android --out $output "$work\aligned.apk"
Check-Exit
& "$tools\apksigner.bat" verify --verbose $output
Check-Exit
Get-FileHash -LiteralPath $output -Algorithm SHA256
