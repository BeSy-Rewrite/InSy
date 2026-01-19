import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { environment } from '../../../environments/environment';
import { build } from '../../../environments/version';

@Component({
  selector: 'app-footer',
  imports: [
    MatButtonModule,
    MatIconModule,
    MatTooltipModule
  ],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css',
})
export class FooterComponent {
  build = build;
  bugReportUrl = environment.bugReportUrl;
  links = environment.footerLinks;
}
