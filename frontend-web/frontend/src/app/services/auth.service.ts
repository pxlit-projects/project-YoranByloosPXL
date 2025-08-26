import { Injectable } from '@angular/core';
import { BehaviorSubject, map } from 'rxjs';

export interface AuthState {
  username: string;
  isEditor: boolean;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly STORAGE_KEY = 'auth_state_v1';

  private readonly _state$ = new BehaviorSubject<AuthState | null>(this.load());
  readonly state$ = this._state$.asObservable();

  readonly isLoggedIn$ = this.state$.pipe(map(s => !!s));
  readonly username$   = this.state$.pipe(map(s => s?.username ?? null));
  readonly isEditor$   = this.state$.pipe(map(s => !!s?.isEditor));

  get state(): AuthState | null { return this._state$.value; }
  get username(): string | null { return this.state?.username ?? null; }
  get isEditor(): boolean       { return !!this.state?.isEditor; }

  login(username: string, isEditor: boolean) {
    const st: AuthState = { username, isEditor };
    this._state$.next(st);
    this.save(st);
  }

  setRole(isEditor: boolean) {
    const st = this.state;
    if (!st) return;
    const updated: AuthState = { ...st, isEditor };
    this._state$.next(updated);
    this.save(updated);
  }

  logout() {
    this._state$.next(null);
    localStorage.removeItem(this.STORAGE_KEY);
  }

  private load(): AuthState | null {
    try {
      const raw = localStorage.getItem(this.STORAGE_KEY);
      return raw ? (JSON.parse(raw) as AuthState) : null;
    } catch {
      return null;
    }
  }

  private save(st: AuthState) {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(st));
  }
}
