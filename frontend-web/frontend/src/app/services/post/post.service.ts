import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable, switchMap, take } from 'rxjs';
import { Post } from '../../models/post.model';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PostService {
  private readonly baseUrl = `${environment.apiBaseUrl}/posts`;
  private auth = inject(AuthService);

  constructor(private http: HttpClient) {}

  getPublished(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.baseUrl}/published`);
  }

  filter(params: { keyword?: string; author?: string; date?: string }): Observable<Post[]> {
    let httpParams = new HttpParams();
    if (params.keyword) httpParams = httpParams.set('keyword', params.keyword);
    if (params.author)  httpParams = httpParams.set('author', params.author);
    if (params.date)    httpParams = httpParams.set('date', params.date);
    return this.http.get<Post[]>(`${this.baseUrl}/filter`, { params: httpParams });
  }

  getById(id: number): Promise<Post> {
    return firstValueFrom(this.http.get<Post>(`${this.baseUrl}/${id}`));
  }

  getByIds(ids: number[]): Observable<Post[]> {
    const params = new HttpParams().set('ids', ids.join(','));
    return this.http.get<Post[]>(`${this.baseUrl}/by-ids`, { params });
  }

  async createDraft(title: string, description: string): Promise<Post> {
    const username = await firstValueFrom(this.auth.username$);
    if (!username) throw new Error('Niet ingelogd');

    const headers = new HttpHeaders().set('username', username);
    const body = { title, description };

    return await firstValueFrom(
      this.http.post<Post>(`${this.baseUrl}?submitForReview=false`, body, { headers })
    );
  }

  getDrafts(): Promise<Post[]> {
    return this.withUserHeaders((headers) =>
      firstValueFrom(this.http.get<Post[]>(`${this.baseUrl}/drafts`, { headers }))
    );
  }

  submitDraft(id: number): Promise<Post> {
    return this.withUserHeaders((headers) =>
      firstValueFrom(this.http.put<Post>(`${this.baseUrl}/${id}/submit`, {}, { headers }))
    );
  }

  updateDraft(id: number, title: string, description: string): Promise<Post> {
    const body = { title, description };
    return this.withUserHeaders((headers) =>
      firstValueFrom(this.http.put<Post>(`${this.baseUrl}/${id}`, body, { headers }))
    );
  }

  private async withUserHeaders<T>(fn: (headers: HttpHeaders) => Promise<T>): Promise<T> {
    const username = await firstValueFrom(this.auth.username$);
    if (!username) throw new Error('Niet ingelogd');
    const headers = new HttpHeaders().set('username', username);
    return fn(headers);
  }

  getMySubmissions(): Observable<Post[]> {
    return this.auth.username$.pipe(
      take(1),
      switchMap(u =>
        this.http.get<Post[]>(`${this.baseUrl}/submissions`, {
          headers: new HttpHeaders().set('username', u ?? '')
        })
      )
    );
  }

  async publish(id: number): Promise<void> {
    const username = await firstValueFrom(this.auth.username$);
    await firstValueFrom(
      this.http.put<void>(`${this.baseUrl}/${id}/publish`, null, {
        headers: new HttpHeaders().set('username', username ?? '')
      })
    );
  }

  async toDraft(id: number): Promise<void> {
    const username = await firstValueFrom(this.auth.username$);
    await firstValueFrom(
      this.http.put<void>(`${this.baseUrl}/${id}/to-draft`, null, {
        headers: new HttpHeaders().set('username', username ?? '')
      })
    );
  }
}
