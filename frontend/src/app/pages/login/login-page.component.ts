import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { AuthenticationService } from '../../services/authentication.service';

@Component({
  selector: 'app-login-page',
  imports: [
    MatButtonModule
  ],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css'
})
export class LoginPageComponent {
  constructor(public authService: AuthenticationService) {
    this.authService = authService;
  }
}
