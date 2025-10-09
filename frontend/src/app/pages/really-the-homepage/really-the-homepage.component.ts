import { AfterViewInit, Component, ElementRef, viewChild } from '@angular/core';

@Component({
  selector: 'app-homepage',
  templateUrl: './really-the-homepage.component.html',
  styleUrl: './really-the-homepage.component.css'
})
export class ReallyTheHomepageComponent implements AfterViewInit {
  iframe = viewChild.required<ElementRef<HTMLIFrameElement>>('iframe');

  ngAfterViewInit() {
    window.addEventListener('keydown', (event) => {
      if (this.iframe().nativeElement.contentWindow) {
        this.iframe().nativeElement.contentWindow?.postMessage(
          { type: 'keydown', key: event.key, code: event.code, altKey: event.altKey, ctrlKey: event.ctrlKey, shiftKey: event.shiftKey, metaKey: event.metaKey },
          window.location.origin
        );
      }
    });
  }
}
