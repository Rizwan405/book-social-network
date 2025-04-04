import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BorroweBookComponent } from './borrowe-book.component';

describe('BorroweBookComponent', () => {
  let component: BorroweBookComponent;
  let fixture: ComponentFixture<BorroweBookComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BorroweBookComponent]
    });
    fixture = TestBed.createComponent(BorroweBookComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
