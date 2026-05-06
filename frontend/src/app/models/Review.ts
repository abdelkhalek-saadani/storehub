export type Review = {
  name: string;
  avatar: string;
  rating: number; // 1–5
  date: string;   // ISO format: YYYY-MM-DD
  title: string;
  comment: string;
}

export type AddReviewParam = Pick<Review, 'title' | 'comment' | 'rating'> & { productId: string }
