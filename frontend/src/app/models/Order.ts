import {CartItem} from './CartItem';

export type Order = {
  id: string,
  userId: string,
  total: number,
  items: CartItem[],
  paymentStatus: 'success' | 'failed'
}
