import { CartItem } from './CartItem';

export interface Order {
  id: string;
  userId: string;
  total: number;
  items: CartItem[];
  paymentStatus: 'success' | 'failed';
}
