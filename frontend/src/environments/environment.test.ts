export const environment = {
    // This is the environment configuration for the frontend application.
    apiUrl: 'https://api.test.insy.hs-esslingen.com',

    /**
     * Regular expression for validating price input formats.
     *
     * Supports both dot and comma as decimal separators, optional thousands separators,
     * optional whitespace, and an optional euro symbol at the end.
     * 
     * Example matches: "12.34", "1,234.56", "1.234,56 €", " 123,45€ "
     *
     * For a visual breakdown, see: https://jex.im/regulex/#!flags=&re=%5E%5Cs*(%5Cd%2B(%5B.%2C%5D%5Cd%7B1%2C2%7D)%3F%7C(%5Cd%7B1%2C3%7D(%5C.%5Cd%7B3%7D)*(%2C%5Cd%7B1%2C2%7D)%3F)%7C(%5Cd%7B1%2C3%7D(%2C%5Cd%7B3%7D)*(%5C.%5Cd%7B1%2C2%7D)%3F))%5Cs*%E2%82%AC%3F%5Cs*%24
     */
    priceRegEx: /^\s*(\d+([.,]\d{1,2})?|(\d{1,3}(\.\d{3})*(,\d{1,2})?)|(\d{1,3}(,\d{3})*(\.\d{1,2})?))\s*€?\s*$/,

    clientId: "angular-app-test",
    issuer: "https://auth.insy.hs-esslingen.com/realms/insy",
    requiredRole: "insy-test"
}
