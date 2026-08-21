export interface UpdateCartRequest {
  storeId: string;
  items: UpdateCartItem[];
}

export interface UpdateCartItem {
  productId: string;
  quantity: number;
}
