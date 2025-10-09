import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReallyTheHomepageComponent } from './really-the-homepage.component';

describe('ReallyTheHomepageComponent', () => {
  let component: ReallyTheHomepageComponent;
  let fixture: ComponentFixture<ReallyTheHomepageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReallyTheHomepageComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ReallyTheHomepageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
