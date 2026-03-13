import { Component, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { Router, RouterModule } from '@angular/router';
import { AuthenticationService } from '../../services/authentication.service';
import { LoginComponent } from "../login-indicator/login.component";
import { NavbarButtonComponent } from '../navbar-button/navbar-button.component';

@Component({
  selector: 'app-homebar',
  imports: [
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    RouterModule,
    LoginComponent,
    NavbarButtonComponent
  ],
  templateUrl: './homebar.component.html',
  styleUrls: ['./homebar.component.css'],
})
export class HomebarComponent {
  isMobileMenuOpen = signal(false);
  activeMenuItem = signal(0);

  links = [
    { name: 'Startseite', path: '/home' },
    { name: 'Inventar', path: '/inventory' },
    { name: 'Bestellungen', path: '/orders' },
    { name: 'Neue Inventarisierung', path: '/new' },
    { name: 'Neue Erweiterung', path: '/new-extension' }
  ];

  constructor(public readonly router: Router, authService: AuthenticationService) {
    router.events.subscribe(() => {
      this.activeMenuItem.set(
        this.links.findIndex(link => link.path === `/${router.parseUrl(router.url).root.children['primary']?.segments[0].path}`)
      );
    });

    if (authService.isAdmin()) {
      this.links.push({ name: 'Import', path: '/import' });
    }
  }

}
