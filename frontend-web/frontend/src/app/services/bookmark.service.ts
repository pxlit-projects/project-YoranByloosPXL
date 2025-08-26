import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { AuthService } from './auth.service';
import { Post } from '../models/post.model';

@Injectable({ providedIn: 'root' })
export class BookmarkService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);

  // Gateway: /bookmarks -> /api/bookmarks
  private readonly baseUrl = 'http://localhost:8084/bookmarks';

  private async headers(): Promise<HttpHeaders> {
    const username = await firstValueFrom(this.auth.username$);
    if (!username) throw new Error('Niet ingelogd');
    return new HttpHeaders().set('username', username);
  }

  /** Voeg bookmark toe voor huidig ingelogde user */
  async add(postId: number): Promise<void> {
    const headers = await this.headers();
    await firstValueFrom(this.http.post<void>(`${this.baseUrl}/${postId}`, {}, { headers }));
  }

  /** Verwijder bookmark */
  async remove(postId: number): Promise<void> {
    const headers = await this.headers();
    await firstValueFrom(this.http.delete<void>(`${this.baseUrl}/${postId}`, { headers }));
  }

  /** Haal al mijn gebookmarkte posts op */
  async getMy(): Promise<Post[]> {
    const headers = await this.headers();
    return await firstValueFrom(this.http.get<Post[]>(this.baseUrl, { headers }));
  }

  /** Check of een post al gebookmarkt is */
  async isBookmarked(postId: number): Promise<boolean> {
    const posts = await this.getMy();
    return posts.some(p => p.id === postId);
  }
}
