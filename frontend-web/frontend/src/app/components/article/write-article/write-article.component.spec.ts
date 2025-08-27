import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WriteArticleComponent } from './write-article.component';

describe('WriteArticleComponent', () => {
  let component: WriteArticleComponent;
  let fixture: ComponentFixture<WriteArticleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WriteArticleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WriteArticleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
