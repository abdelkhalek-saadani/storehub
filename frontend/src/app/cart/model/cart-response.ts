import { UUID } from 'node:crypto';

export interface CartResponse {
  cartId: string;
  items: CartItemResponse[];
  originalTotal: number;
  finalTotal: number;
  totalDiscount: number;
  storeId: string;
}

export interface CartItemResponse {
  itemId: UUID;
  productId: UUID;
  productName: string;
  productImageUrl: string;
  quantity: number;
  unitPrice: number;
  originalLineTotal: number;
  discountAmount: number;
  finalLineTotal: number;
  appliedOfferLabel: string;
}
