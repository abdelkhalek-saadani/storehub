import { UUID } from 'node:crypto';

export interface CartResponse {
  cartId: UUID;
  items: CartItemResponse[];
  originalTotal: number;
  finalTotal: number;
  totalDiscount: number;
  storeId: UUID;
}

export interface CartItemResponse {
  itemId: UUID;
  productId: UUID;
  quantity: number;
  unitPrice: number;
  originalLineTotal: number;
  discountAmount: number;
  finalLineTotal: number;
  appliedOfferLabel: string;
}
