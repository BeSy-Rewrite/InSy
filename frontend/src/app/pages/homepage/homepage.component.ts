import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css'
})
export class HomepageComponent implements AfterViewInit {
  @ViewChild('iframe') iframe!: ElementRef<HTMLIFrameElement>;

  ngAfterViewInit() {
    window.addEventListener('keydown', (event) => {
      if (this.iframe?.nativeElement?.contentWindow) {
        this.iframe.nativeElement.contentWindow.postMessage(
          { type: 'keydown', key: event.key, code: event.code, altKey: event.altKey, ctrlKey: event.ctrlKey, shiftKey: event.shiftKey, metaKey: event.metaKey },
          window.location.origin
        );
      }
    });
  }
}
