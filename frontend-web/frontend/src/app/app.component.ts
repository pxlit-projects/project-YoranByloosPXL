import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavComponent } from "./components/nav/nav.component";
import { NavAdminComponent } from "./components/nav-admin/nav-admin.component";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavComponent, NavAdminComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
}
