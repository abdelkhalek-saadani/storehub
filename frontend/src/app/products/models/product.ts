import { DiscountSummary } from './discount-summary';

export interface Product {
  id: string;
  storeId: string;
  name: string;
  description: string | null;
  unitPrice: number;
  activeDiscount: DiscountSummary | null;
  finalPrice: number;
}
