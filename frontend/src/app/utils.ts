import { Article } from "./models/Article";
import { InventoryItem } from "./models/inventory-item";

/**
 * Converts a price value to a localized string representation with a comma as decimal separator
 * and appends the Euro sign.
 *
 * @param price - The price value as a number or string.
 * @returns The localized price string (e.g., "12,34 €").
 */
export function localizePrice(price: number | string): string {
    let numPrice: number;
    if (typeof price === 'number') {
        numPrice = price;
    } else {
        if (price === '' || Number.isNaN(Number.parseFloat(price))) {
            return '';
        }
        numPrice = Number.parseFloat(price);
    }
    return numPrice.toLocaleString('de-DE', { minimumFractionDigits: 2 }).concat('\xa0€').trim();
}

/**
 * Converts a localized price string back to a numeric value.
 * Handles both dot and comma as decimal and thousands separators.
 * Removes any Euro symbol and whitespace.
 * If the input is already a number, it returns it directly. If the input is invalid, it returns 0.
 *
 * @param priceInput - The localized price string (e.g., "12,34 €").
 * @returns The numeric price value.
 */
export function unLocalizePrice(priceInput: string | number): number {
    if (typeof priceInput === 'string') {
        priceInput = priceInput.replace('€', '').trim();
        // Remove thousand separators (either '.' or ',') that are followed by exactly three digits
        priceInput = priceInput.replaceAll(/([.,])(?=\d{3})/g, '');

        const price = Number.parseFloat(String(priceInput).replace(',', '.'));
        return Number.isNaN(price) ? 0 : price;
    } else if (typeof priceInput === 'number') {
        return priceInput;
    } else {
        return 0;
    }
}

/**
 * Converts an Article object to an InventoryItem object.
 *
 * @param article - The Article object to convert.
 * @returns The converted InventoryItem object.
 */
export function inventoryItemFromArticle(article: Article, costCenter: string): InventoryItem {
    return {
        id: article.inventories_id,
        description: article.description,
        serial_number: article.inventories_serial_number,
        price: article.price,
        location: article.location,
        cost_center: costCenter,
        company: article.company,
        orderer: article.orderer,
        is_deinventoried: false,
        created_at: new Date().toISOString(),
        tags: []
    } as InventoryItem;
}
