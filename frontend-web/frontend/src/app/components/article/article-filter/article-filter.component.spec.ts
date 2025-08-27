import { TestBed } from '@angular/core/testing';
import { ArticleFilterComponent } from './article-filter.component';

describe('ArticleFilterComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [ArticleFilterComponent],
    }).compileComponents();

    const fixture = TestBed.createComponent(ArticleFilterComponent);
    const comp = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, comp };
  }

  it('emit met getrimde waarden + lokale ISO datum op submit', async () => {
    const { comp } = await setup();
    const emit = spyOn(comp.search, 'emit');

    comp.form.setValue({
      keyword: '  hallo  ',
      author: '  ann ',
      dateOnly: '2025-01-15'
    });
    comp.submit();

    expect(emit).toHaveBeenCalledOnceWith({
      keyword: 'hallo',
      author: 'ann',
      date: '2025-01-15T00:00:00',
    });
  });

  it('clear reset en emit {}', async () => {
    const { comp } = await setup();
    const emit = spyOn(comp.search, 'emit');

    comp.form.patchValue({ keyword: 'x' });
    comp.clear();

    expect(comp.form.value).toEqual({ keyword: '', author: '', dateOnly: '' });
    expect(emit).toHaveBeenCalledOnceWith({});
  });
});
