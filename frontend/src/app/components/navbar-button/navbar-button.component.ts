import { NgClass } from '@angular/common';
import { Component, input, signal } from '@angular/core';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { of, startWith, switchMap } from 'rxjs';
import { AuthenticationService } from '../../services/authentication.service';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-navbar-button',
  templateUrl: './navbar-button.component.html',
  imports: [
    MatButtonModule,
    MatBadgeModule,
    MatTooltipModule,
    RouterModule,
    NgClass
  ],
  styleUrls: ['./navbar-button.component.scss']
})
export class NavbarButtonComponent {
  label = input.required<string>();
  routerLink = input.required<string>();
  isExact = input<boolean>(false);
  extraClasses = input<string>("");

  numberOfOpenArticles = signal<number>(0);


  constructor(public authService: AuthenticationService,
    private readonly orderService: OrderService) { }

  /**
   * Initializes the component and sets up a subscription to track the number of open articles.
   * It uses the OrderService to fetch the number of open articles whenever there is a change
   * in the open articles list, ensuring that the displayed count is always up-to-date.
   */
  ngOnInit() {
    this.orderService.openArticlesChanged.pipe(
      startWith(null), // Trigger the initial load
      switchMap(() => {
        if (this.authService.hasValidToken()) {
          return this.orderService.getNumberOfOpenArticles()
        } else {
          return of(0); // Return 0 if the user is not authenticated
        }
      })
    ).subscribe((count) => {
      this.numberOfOpenArticles.set(count);
    });
  }
}
