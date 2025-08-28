import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { Post } from '../../models/post.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BookmarkService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);

  private readonly baseUrl = `${environment.apiBaseUrl}/bookmarks`;

  private async headers(): Promise<HttpHeaders> {
    const username = await firstValueFrom(this.auth.username$);
    if (!username) throw new Error('Niet ingelogd');
    return new HttpHeaders().set('username', username);
  }

  async add(postId: number): Promise<void> {
    const headers = await this.headers();
    await firstValueFrom(this.http.post<void>(`${this.baseUrl}/${postId}`, {}, { headers }));
  }

  async remove(postId: number): Promise<void> {
    const headers = await this.headers();
    await firstValueFrom(this.http.delete<void>(`${this.baseUrl}/${postId}`, { headers }));
  }

  async getMy(): Promise<Post[]> {
    const headers = await this.headers();
    return await firstValueFrom(this.http.get<Post[]>(this.baseUrl, { headers }));
  }

  async isBookmarked(postId: number): Promise<boolean> {
    const posts = await this.getMy();
    return posts.some(p => p.id === postId);
  }
}
