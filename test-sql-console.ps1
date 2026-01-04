# SQL Console Test Script
Write-Host "`n=== SQL CONSOLE API TEST ===" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api/admin/sql"
$headers = @{"Content-Type"="application/json"}

# Test 1: Execute SQL Query
Write-Host "`n1. Testing SQL Execute Endpoint..." -ForegroundColor Yellow
try {
    $body = @{
        query = "SELECT user_id, name, email, role FROM users LIMIT 3"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/execute" -Method POST -Headers $headers -Body $body
    Write-Host "   ✅ SUCCESS!" -ForegroundColor Green
    Write-Host "   Rows returned: $($response.rowCount)" -ForegroundColor White
    Write-Host "   Columns: $($response.columns -join ', ')" -ForegroundColor White
    Write-Host "`n   Sample Data:" -ForegroundColor Cyan
    $response.rows | Select-Object -First 2 | ForEach-Object {
        Write-Host "   - $($_.name) <$($_.email)> [$($_.role)]" -ForegroundColor Gray
    }
} catch {
    Write-Host "   ❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: List Tables
Write-Host "`n2. Testing List Tables Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/tables" -Method GET
    Write-Host "   ✅ SUCCESS!" -ForegroundColor Green
    Write-Host "   Tables found: $($response.tables.Count)" -ForegroundColor White
    Write-Host "`n   Available Tables:" -ForegroundColor Cyan
    $response.tables | Select-Object -First 10 | ForEach-Object {
        Write-Host "   - $_" -ForegroundColor Gray
    }
} catch {
    Write-Host "   ❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Get Column Info
Write-Host "`n3. Testing Get Columns Endpoint..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/columns/users" -Method GET
    Write-Host "   ✅ SUCCESS!" -ForegroundColor Green
    Write-Host "   Columns in 'users' table: $($response.columns.Count)" -ForegroundColor White
    Write-Host "`n   Column Details:" -ForegroundColor Cyan
    $response.columns | Select-Object -First 5 | ForEach-Object {
        Write-Host "   - $($_.column_name) ($($_.data_type)) - Nullable: $($_.is_nullable)" -ForegroundColor Gray
    }
} catch {
    Write-Host "   ❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Security - Try Invalid Query (DELETE)
Write-Host "`n4. Testing Security (should block DELETE)..." -ForegroundColor Yellow
try {
    $body = @{
        query = "DELETE FROM users WHERE user_id = 1"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/execute" -Method POST -Headers $headers -Body $body -ErrorAction Stop
    Write-Host "   ❌ SECURITY FAILED: DELETE was allowed!" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 400) {
        Write-Host "   ✅ SECURITY PASSED: DELETE blocked correctly" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️  UNEXPECTED: $($_.Exception.Message)" -ForegroundColor Yellow
    }
}

# Test 5: Security - Try Invalid Query (DROP)
Write-Host "`n5. Testing Security (should block DROP)..." -ForegroundColor Yellow
try {
    $body = @{
        query = "DROP TABLE users"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/execute" -Method POST -Headers $headers -Body $body -ErrorAction Stop
    Write-Host "   ❌ SECURITY FAILED: DROP was allowed!" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 400) {
        Write-Host "   ✅ SECURITY PASSED: DROP blocked correctly" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️  UNEXPECTED: $($_.Exception.Message)" -ForegroundColor Yellow
    }
}

Write-Host "`n=== TEST SUITE COMPLETED ===" -ForegroundColor Cyan
Write-Host ""
