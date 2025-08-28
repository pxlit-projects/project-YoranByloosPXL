import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Notification {
  id: number;
  recipient: string;
  message: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/notifications`;

  getForUser(user: string): Observable<Notification[]> {
    const params = new HttpParams().set('user', user);
    return this.http.get<Notification[]>(this.baseUrl, { params });
  }

  deleteOne(id: number, user: string): Observable<void> {
    const params = new HttpParams().set('user', user);
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { params });
  }

  deleteAll(user: string): Observable<void> {
    const params = new HttpParams().set('user', user);
    return this.http.delete<void>(this.baseUrl, { params });
  }
}
