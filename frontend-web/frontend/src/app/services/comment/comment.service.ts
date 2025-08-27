import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { Comment } from '../../models/comment.model';
import { AuthService } from '../auth/auth.service';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private readonly baseUrl = 'http://localhost:8084/comments';
  private auth = inject(AuthService);

  constructor(private http: HttpClient) {}

  getByPostId(postId: number) {
    return this.http.get<Comment[]>(`${this.baseUrl}/${postId}`);
  }

  async add(postId: number, content: string): Promise<Comment> {
    const username = await firstValueFrom(this.auth.username$);
    if (!username) throw new Error('Je bent niet ingelogd');
    const headers = new HttpHeaders().set('username', username);
    const dto = { postId, content };
    return await firstValueFrom(this.http.post<Comment>(this.baseUrl, dto, { headers }));
  }

  async update(commentId: number, content: string): Promise<Comment> {
    const username = await firstValueFrom(this.auth.username$);
    if (!username) throw new Error('Je bent niet ingelogd');
    const headers = new HttpHeaders().set('username', username);
    const dto = { content };
    return await firstValueFrom(this.http.put<Comment>(`${this.baseUrl}/${commentId}`, dto, { headers }));
  }

  async delete(commentId: number): Promise<void> {
    const username = await firstValueFrom(this.auth.username$);
    if (!username) throw new Error('Je bent niet ingelogd');
    const headers = new HttpHeaders().set('username', username);
    await firstValueFrom(this.http.delete<void>(`${this.baseUrl}/${commentId}`, { headers }));
  }
}
