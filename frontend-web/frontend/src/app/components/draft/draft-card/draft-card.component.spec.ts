import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { DraftCardComponent } from './draft-card.component';
import { Post } from '../../../models/post.model';

describe('DraftCardComponent', () => {
  const post: Post = {
    id: 42,
    title: 'Titel',
    content: 'Desc',
    author: 'ann',
    status: 'CONCEPT',
    published: false,
    createdAt: '2024-01-01T00:00:00',
    updatedAt: '2024-01-02T00:00:00',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DraftCardComponent],
    }).compileComponents();
  });

  it('emit publish met post en zet publishing=true (maar geen tweede keer)', () => {
    const fixture = TestBed.createComponent(DraftCardComponent);
    const comp = fixture.componentInstance;
    comp.post = post;

    const spy = spyOn(comp.publish, 'emit');

    comp.onPublish();
    expect(spy).toHaveBeenCalledOnceWith(post);
    expect(comp.publishing).toBeTrue();

    comp.onPublish();
    expect(spy).toHaveBeenCalledTimes(1);
  });

  it('emit edit met post', () => {
    const fixture = TestBed.createComponent(DraftCardComponent);
    const comp = fixture.componentInstance;
    comp.post = post;

    const spy = spyOn(comp.edit, 'emit');

    comp.onEdit();
    expect(spy).toHaveBeenCalledOnceWith(post);
  });

  it('disable publish-knop wanneer publishing=true', () => {
    const fixture = TestBed.createComponent(DraftCardComponent);
    const comp = fixture.componentInstance;
    comp.post = post;
    comp.publishing = true;

    fixture.detectChanges();

    const btn = fixture.debugElement.query(By.css('.btn.btn--primary')).nativeElement as HTMLButtonElement;
    expect(btn.disabled).toBeTrue();
  });
});
