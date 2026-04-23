import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatButtonModule } from "@angular/material/button";
import { MatDialogModule } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { BrowserMultiFormatReader, IScannerControls } from '@zxing/browser';
import { CardComponent } from '../../components/card/card.component';
import { InventoryItem } from '../../models/inventory-item';
import { InventoriesService } from '../../services/inventories.service';

@Component({
  selector: 'app-scanner',
  imports: [
    CardComponent,
    MatButtonModule,
    MatDialogModule
  ],
  templateUrl: './scanner.component.html',
  styleUrl: './scanner.component.css',
})
export class ScannerComponent implements OnInit, OnDestroy {

  @ViewChild('videoElement') private readonly videoElement?: ElementRef<HTMLVideoElement>;

  isScanning = false;
  hasScannerSupport = typeof navigator !== 'undefined' && !!navigator.mediaDevices?.getUserMedia;
  cameraError = '';
  lastScannedValue = '';
  lastScannedFormat = '';
  scannedItem: InventoryItem | null = null;
  isLoadingItem = false;
  itemLoadError = '';

  private readonly codeReader = new BrowserMultiFormatReader();
  private scannerControls?: IScannerControls;

  constructor(
    private readonly router: Router,
    private readonly inventoriesService: InventoriesService
  ) { }

  ngOnInit(): void {
    void this.startScanner();
  }

  ngOnDestroy(): void {
    this.stopScanner();
  }

  async startScanner(): Promise<void> {
    if (!this.hasScannerSupport || this.isScanning) {
      return;
    }

    this.cameraError = '';
    this.lastScannedValue = '';
    this.lastScannedFormat = '';

    const video = this.videoElement?.nativeElement;
    if (!video) {
      this.cameraError = 'Scanner konnte nicht gestartet werden.';
      return;
    }

    try {
      this.isScanning = true;

      this.scannerControls = await this.codeReader.decodeFromVideoDevice(
        undefined,
        video,
        (result) => {
          if (result) {
            this.lastScannedValue = result.getText();
            this.lastScannedFormat = String(result.getBarcodeFormat());
            this.stopScanner();
            void this.loadScannedItem();
          }
        }
      );
    } catch {
      this.cameraError = 'Kein Kamerazugriff möglich. Bitte Berechtigungen prüfen.';
      this.stopScanner();
    }
  }

  stopScanner(): void {
    this.scannerControls?.stop();
    this.scannerControls = undefined;

    const video = this.videoElement?.nativeElement;
    if (video) {
      video.pause();
      video.srcObject = null;
    }

    this.isScanning = false;
    this.cameraError = '';
  }

  clearResult(): void {
    this.lastScannedValue = '';
    this.lastScannedFormat = '';
    this.scannedItem = null;
    this.itemLoadError = '';
    void this.startScanner();
  }

  private async loadScannedItem(): Promise<void> {
    if (!this.lastScannedValue) {
      return;
    }

    this.isLoadingItem = true;
    this.itemLoadError = '';

    try {
      const itemId = Number.parseInt(this.lastScannedValue, 10);
      if (Number.isNaN(itemId)) {
        this.itemLoadError = 'Ungültige Inventarnummer im Barcode.';
        this.isLoadingItem = false;
        return;
      }

      this.inventoriesService.getInventoryById(itemId).subscribe({
        next: (item) => {
          this.scannedItem = item;
          this.isLoadingItem = false;
        },
        error: () => {
          this.itemLoadError = 'Inventaritem konnte nicht gefunden werden.';
          this.isLoadingItem = false;
        }
      });
    } catch {
      this.itemLoadError = 'Fehler beim Laden des Items.';
      this.isLoadingItem = false;
    }
  }

  navigateToItem(): void {
    if (this.scannedItem?.id) {
      void this.router.navigate(['/inventory', this.scannedItem.id]);
    }
  }

  resumeScanning(): void {
    this.clearResult();
    this.startScanner();
  }

}
