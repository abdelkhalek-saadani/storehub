export interface DiscountSummary {
  id: string;
  type: 'PERCENTAGE' | 'QUANTITY' | 'BUY_X_GET_Y';
  startsAt: string;
  endsAt: string;
}
