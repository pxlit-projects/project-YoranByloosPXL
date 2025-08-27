import { Component, HostListener, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth/auth.service';
import { NotificationService, Notification } from '../../../services/notification/notification.service';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-notification-dropdown',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-dropdown.component.html',
  styleUrls: ['./notification-dropdown.component.css']
})
export class NotificationDropdownComponent {
  auth = inject(AuthService);
  private api = inject(NotificationService);

  open = signal(false);
  loading = signal(false);
  error = signal('');
  items = signal<Notification[]>([]);

  async toggle() {
    this.open.set(!this.open());
    if (this.open() && this.items().length === 0) {
      await this.refresh();
    }
  }

  async refresh() {
    this.loading.set(true);
    this.error.set('');
    try {
      const user = await firstValueFrom(this.auth.username$);
      if (!user) return; // niet ingelogd
      const list = await firstValueFrom(this.api.getForUser(user));
      this.items.set(list);
    } catch {
      this.error.set('Kon meldingen niet laden');
    } finally {
      this.loading.set(false);
    }
  }

  async removeOne(n: Notification) {
    try {
      const user = await firstValueFrom(this.auth.username$);
      if (!user) return;
      await firstValueFrom(this.api.deleteOne(n.id, user));
      this.items.update(arr => arr.filter(x => x.id !== n.id));
    } catch {
      // laat error stil
    }
  }

  // klik buiten sluit dropdown
  @HostListener('document:click', ['$event'])
  onDocClick(ev: MouseEvent) {
    const target = ev.target as HTMLElement;
    if (!target.closest('.notif-root')) {
      this.open.set(false);
    }
  }
}
