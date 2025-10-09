import { TestBed } from '@angular/core/testing';

import { CachedInventoryService } from './cached-inventory.service';

describe('CachedInventoryService', () => {
  let service: CachedInventoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CachedInventoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
