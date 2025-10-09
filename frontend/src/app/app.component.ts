import { ScrollingModule } from '@angular/cdk/scrolling';
import { Component } from '@angular/core';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterOutlet } from '@angular/router';
import { HomebarComponent } from "./components/homebar/homebar.component";

@Component({
  standalone: true,
  selector: 'app-root',
  imports: [
    RouterOutlet,
    MatButtonModule,
    MatBadgeModule,
    MatTooltipModule,
    ScrollingModule,
    HomebarComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {

}
