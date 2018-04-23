# Change Log

## [2.0.3]
### Updated
- Change SQL endpoint from `/query/sql` to `sql`
- Improve client tests

## [2.0.2]
### Updated
- Correct data extraction validator to accept columns: all
- Add support  for SQL queries on client
- Adapt test queries to the changes on SlicingDice API
- Change HTTP connection to be async

## [2.0.1]
### Updated
- Improve exception throwing in case of JSON parsing error.

## [2.0.0]
### Updated
- Update `existsEntity` to receive `table` as parameter
- Update API errors code
- Rename SlicingDice API endpoints

## [1.0.0]
### Added
- Thin layers around SlicingDice API endpoints
- Automatic regression test script RunQueryTests.java with its JSON data
- Tests for data extraction
