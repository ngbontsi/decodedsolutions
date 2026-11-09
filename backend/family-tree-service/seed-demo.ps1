# Re-creates the demo family used in the UI walkthrough.
# Safe to re-run: it stops if the archive already has people.
#
# Usage (service running on 8085):
#   .\seed-demo.ps1
#   .\seed-demo.ps1 -BaseUrl "http://localhost:8085/api/v1"

param(
    [string]$BaseUrl = "http://localhost:8085/api/v1"
)

$ErrorActionPreference = "Stop"

function PostJson($path, $body) {
    Invoke-RestMethod -Uri "$BaseUrl$path" -Method Post `
        -ContentType "application/json" -Body ($body | ConvertTo-Json -Depth 6)
}

function PatchJson($path, $body) {
    Invoke-RestMethod -Uri "$BaseUrl$path" -Method Patch `
        -ContentType "application/json" -Body ($body | ConvertTo-Json -Depth 6)
}

$existing = @(Invoke-RestMethod "$BaseUrl/persons")
if ($existing.Count -gt 0) {
    Write-Host "Archive already has $($existing.Count) people. Nothing to do."
    exit 0
}

# People are created without names first: a person can exist unnamed.
$gogo = PostJson "/persons" @{
    gender = "FEMALE"; birthDate = "1928-01-01"; birthDatePrecision = "YEAR"
    visibility = "FAMILY"; biography = "Family matriarch. Remembers the move from Ngcobo."
}
$baba = PostJson "/persons" @{
    gender = "MALE"; birthDate = "1952-01-01"; birthDatePrecision = "YEAR"; visibility = "FAMILY"
}
$sisi = PostJson "/persons" @{
    gender = "FEMALE"; birthDate = "1978-01-01"; birthDatePrecision = "YEAR"; visibility = "FAMILY"
}
$thabo = PostJson "/persons" @{
    gender = "MALE"; birthDate = "1981-01-01"; birthDatePrecision = "YEAR"; visibility = "FAMILY"
}

PostJson "/persons/$($gogo.id)/names" @{ nameType = "BIRTH"; fullName = "Gogo Nene"; givenNames = "Gogo"; familyName = "Nene"; preferred = $true } | Out-Null
PostJson "/persons/$($baba.id)/names" @{ nameType = "BIRTH"; fullName = "Baba Nene"; givenNames = "Baba"; familyName = "Nene"; preferred = $true } | Out-Null
PostJson "/persons/$($sisi.id)/names" @{ nameType = "BIRTH"; fullName = "Sisi Nene"; givenNames = "Sisi"; familyName = "Nene"; preferred = $true } | Out-Null
PostJson "/persons/$($thabo.id)/names" @{ nameType = "BIRTH"; fullName = "Thabo Nene"; givenNames = "Thabo"; familyName = "Nene"; preferred = $true } | Out-Null

# Four parent links, including one parent the family knows of but has not identified.
PostJson "/parentages" @{ childId = $baba.id; parentId = $gogo.id; parentageType = "BIOLOGICAL"; parentRole = "MOTHER"; identityStatus = "IDENTIFIED" } | Out-Null
PostJson "/parentages" @{ childId = $baba.id; parentageType = "BIOLOGICAL"; parentRole = "FATHER"; identityStatus = "UNIDENTIFIED" } | Out-Null
PostJson "/parentages" @{ childId = $thabo.id; parentId = $baba.id; parentageType = "BIOLOGICAL"; parentRole = "FATHER"; identityStatus = "IDENTIFIED" } | Out-Null
PostJson "/parentages" @{ childId = $sisi.id; parentId = $baba.id; parentageType = "BIOLOGICAL"; parentRole = "FATHER"; identityStatus = "IDENTIFIED" } | Out-Null

$family = PostJson "/families" @{
    name = "Nene"; description = "The Nene family of Ngcobo and Cape Town."; lineageType = "BILATERAL"
}
PostJson "/families/$($family.id)/members" @{ personId = $baba.id; connectionType = "DESCENDANT"; basis = "PARENTAGE" } | Out-Null

$source = PostJson "/evidence/sources" @{
    sourceType = "ORAL_HISTORY"; title = "Interview with Gogo Nene, 2019"; authorOrSpeaker = "Gogo Nene"
}
$doc = PostJson "/evidence/sources/$($source.id)/documents" @{
    mediaType = "TESTIMONY"; description = "First-hand testimony: born in Ngcobo in 1928."
}

$claim = PostJson "/claims" @{
    subjectPersonId = $gogo.id; statement = "Gogo Nene was born in Ngcobo in 1928"
}
PostJson "/claims/$($claim.id)/evidence" @{ documentId = $doc.id; stance = "SUPPORTS"; weight = "STRONG"; note = "First-hand testimony" } | Out-Null
PatchJson "/claims/$($claim.id)/status" @{ status = "UNDER_REVIEW" } | Out-Null

Write-Host "Seeded 4 people, 4 parent links, 1 family, 1 source and 1 claim."
