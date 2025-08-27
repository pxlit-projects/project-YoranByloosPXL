import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ArticleCardComponent } from './article-card.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('ArticleCardComponent', () => {
  let fixture: ComponentFixture<ArticleCardComponent>;
  let component: ArticleCardComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArticleCardComponent, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleCardComponent);
    component = fixture.componentInstance;
  });

  function setPost(p: Partial<any>) {
    component.post = {
      id: 1,
      title: 'Mijn titel',
      content: 'Korte inhoud',
      author: 'Piet',
      createdAt: new Date(2025, 0, 2).toString(),
      updatedAt: new Date(2025, 0, 2).toString(), 
      published: true,
      status: 'GEPUBLICEERD',
      ...p
    };
    fixture.detectChanges();
  }

  it('rendered de titel, datum en auteur', () => {
    setPost({});
    const title = fixture.debugElement.query(By.css('.card__title')).nativeElement as HTMLElement;
    const meta  = fixture.debugElement.query(By.css('.date')).nativeElement as HTMLElement;

    expect(title.textContent?.trim()).toBe('Mijn titel');
    expect(meta.textContent).toContain('02/01/2025');
    expect(meta.textContent).toContain('Piet');
  });

  it('toont volledige tekst als content ≤ 220 chars (zonder ellipsis)', () => {
    const content = 'Dit is kort.';
    setPost({ content });
    const text = fixture.debugElement.query(By.css('.card__text')).nativeElement as HTMLElement;

    expect(text.textContent?.trim()).toBe(content);
    expect(text.textContent).not.toContain('…');
  });

  it('truncate content > 220 chars en eindigt met “…”', () => {
    const long = 'x'.repeat(230);
    setPost({ content: long });
    const text = fixture.debugElement.query(By.css('.card__text')).nativeElement as HTMLElement;

    const shown = text.textContent?.trim() ?? '';
    expect(shown.endsWith('…')).toBeTrue();
    expect(shown.length).toBe(221);
  });

  it('linkt naar detailpagina via routerLink', () => {
    setPost({ id: 42 });
    const a = fixture.debugElement.query(By.css('.btn')).nativeElement as HTMLAnchorElement;

    const reflected = (a.getAttribute('ng-reflect-router-link') ?? '').replace(/\s/g, '');
    expect(reflected).toBe('/posts,42');
  });
});
