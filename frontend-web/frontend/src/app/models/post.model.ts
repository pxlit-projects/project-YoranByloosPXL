export type PostStatus =
  | 'CONCEPT'
  | 'INGEDIEND'
  | 'GOEDGEKEURD'
  | 'GEWEIGERD'
  | 'GEPUBLICEERD';

export interface Post {
  id: number;
  title: string;
  content: string;
  author: string;
  status: PostStatus;
  published: boolean;
  createdAt: string;
  updatedAt: string;
}
