import { Article } from "./Article";

export interface Order {
  id: number,
  description: string,
  price: number,
  company: string,
  order_date: string,
  cost_center: string,
  orderer: string,
  articles: Article[],
  besy_id: number,
  order_number?: string,
}

export interface ArticleId {
  orderId: number;
  articleId: number;
}

export const ORDER_ID_ARTICLE_ID_SEPARATOR = '-';
