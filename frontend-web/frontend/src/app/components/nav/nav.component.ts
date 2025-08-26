import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AsyncPipe, NgIf } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { firstValueFrom } from 'rxjs';
import { Location } from '@angular/common';

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [RouterLink, AsyncPipe, NgIf],
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css']
})
export class NavComponent {
  private router = inject(Router);
  auth = inject(AuthService);

  constructor(private location: Location) {}

  async onUserClick() {
    const loggedIn = await firstValueFrom(this.auth.isLoggedIn$);
    if (!loggedIn) {
      this.router.navigate(['/login']);
    } else {
      this.auth.logout();
      this.router.navigate(['/']);
      window.location.reload()
    }
  }
}
