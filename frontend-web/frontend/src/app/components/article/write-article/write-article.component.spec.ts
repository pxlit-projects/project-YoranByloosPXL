import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, sample } from 'rxjs';

import { WriteArticleComponent } from './write-article.component';
import { PostService } from '../../../services/post/post.service';
import { AuthService } from '../../../services/auth/auth.service';
import { Post } from '../../../models/post.model';

const samplePost: Post = {
  id: 123,
  title: 'Titel',
  content: 'Beschrijving',
  author: 'ann',
  status: 'GEPUBLICEERD',
  published: false,
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString(),
};

describe('WriteArticleComponent', () => {
  let posts: jasmine.SpyObj<PostService>;

  beforeEach(async () => {
    posts = jasmine.createSpyObj<PostService>('PostService', ['createDraft']);

    await TestBed.configureTestingModule({
      imports: [WriteArticleComponent],
      providers: [
        { provide: PostService, useValue: posts },
        { provide: AuthService, useValue: { username$: of('ann') } }, // nodig door inject()
        provideRouter([]),
      ],
    }).compileComponents();
  });

  it('slaagt draft op bij geldige form en toont successMsg', async () => {
    posts.createDraft.and.returnValue(Promise.resolve(samplePost));

    const fixture = TestBed.createComponent(WriteArticleComponent);
    const comp = fixture.componentInstance;

    comp.form.setValue({ title: 'Titel', description: 'Beschrijving' });
    await comp.saveDraft();
    fixture.detectChanges();

    expect(posts.createDraft).toHaveBeenCalledOnceWith('Titel', 'Beschrijving');
    expect(comp.successMsg).toBe('Draft opgeslagen.');
    expect(comp.errorMsg).toBe('');
    expect(comp.saving).toBeFalse();
    expect(comp.form.pristine).toBeTrue();
  });

  it('markeert form touched en slaat niet op bij ongeldige form', async () => {
    posts.createDraft.and.returnValue(Promise.resolve(samplePost));

    const fixture = TestBed.createComponent(WriteArticleComponent);
    const comp = fixture.componentInstance;

    // leeg = invalid
    await comp.saveDraft();

    expect(posts.createDraft).not.toHaveBeenCalled();
    expect(comp.saving).toBeFalse();
    expect(comp.successMsg).toBe('');
  });

  it('toont errorMsg als opslaan faalt', async () => {
    posts.createDraft.and.returnValue(Promise.reject(new Error('boom')));

    const fixture = TestBed.createComponent(WriteArticleComponent);
    const comp = fixture.componentInstance;
    comp.form.setValue({ title: 'Ok', description: 'Desc' });

    await comp.saveDraft();
    fixture.detectChanges();

    expect(posts.createDraft).toHaveBeenCalled();
    expect(comp.errorMsg).toBe('Opslaan mislukt. Probeer later opnieuw.');
    expect(comp.successMsg).toBe('');
    expect(comp.saving).toBeFalse();
  });
});
