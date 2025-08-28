import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, firstValueFrom, switchMap, take } from 'rxjs';
import { Post } from '../../models/post.model';
import { AuthService } from '../auth/auth.service';
import { environment } from '../../../environments/environment';
import { Review } from '../../models/review.model';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private readonly baseUrl = `${environment.apiBaseUrl}/reviews`;
  private auth = inject(AuthService);

  constructor(private http: HttpClient) {}

  getReviewablePosts(): Observable<Post[]> {
    return this.auth.username$.pipe(
      take(1),
      switchMap(username => {
        const headers = new HttpHeaders().set('username', username ?? '');
        
        return this.http.get<Post[]>(`${this.baseUrl}/reviewable`, { headers });
      })
    );
  }

  getByPostId(postId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.baseUrl}/${postId}`);
  }

  async approve(postId: number): Promise<void> {
    await firstValueFrom(this.http.post<void>(`${this.baseUrl}/${postId}/approve`, {}));
  }

  async reject(postId: number, comment: string): Promise<void> {
    const username = await firstValueFrom(this.auth.username$);
    const headers = new HttpHeaders().set('username', username ?? '');
    const params  = new HttpParams().set('comment', comment);
    await firstValueFrom(this.http.post<void>(`${this.baseUrl}/${postId}/reject`, {}, { headers, params }));
  }
}
