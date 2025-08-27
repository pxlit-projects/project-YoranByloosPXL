import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DraftCardComponent } from './draft-card.component';

describe('DraftCardComponent', () => {
  let component: DraftCardComponent;
  let fixture: ComponentFixture<DraftCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DraftCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DraftCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
