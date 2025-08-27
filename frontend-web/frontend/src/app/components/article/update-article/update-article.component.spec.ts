import { TestBed } from '@angular/core/testing';
import { convertToParamMap, ActivatedRoute, Router } from '@angular/router';

import { UpdateArticleComponent } from './update-article.component';
import { PostService } from '../../../services/post/post.service';
import { Post } from '../../../models/post.model';

describe('UpdateArticleComponent', () => {
  let postSvc: jasmine.SpyObj<PostService>;
  let router: jasmine.SpyObj<Router>;

  const routeMock: Partial<ActivatedRoute> = {
    snapshot: { paramMap: convertToParamMap({ id: '5' }) } as any,
  };

  const post: Post = {
    id: 5,
    title: 'Originele titel',
    content: 'Originele inhoud',
    author: 'ann',
    status: 'CONCEPT',
    published: false,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };

  beforeEach(async () => {
    postSvc = jasmine.createSpyObj<PostService>('PostService', ['getById', 'updateDraft']);
    router  = jasmine.createSpyObj<Router>('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [UpdateArticleComponent],
      providers: [
        { provide: ActivatedRoute, useValue: routeMock },
        { provide: Router, useValue: router },
        { provide: PostService, useValue: postSvc },
      ],
    }).compileComponents();
  });

  it('laadt post en vult formulier', async () => {
    postSvc.getById.and.returnValue(Promise.resolve(post));

    const fixture = TestBed.createComponent(UpdateArticleComponent);
    fixture.detectChanges();

    await fixture.whenStable();
    fixture.detectChanges();

    const comp = fixture.componentInstance;
    expect(comp.form.value.title).toBe('Originele titel');
    expect(comp.form.value.description).toBe('Originele inhoud');
    expect(comp.loading).toBeFalse();
  });

  it('toont fout wanneer laden faalt', async () => {
    postSvc.getById.and.returnValue(Promise.reject('boom'));

    const fixture = TestBed.createComponent(UpdateArticleComponent);
    fixture.detectChanges();

    await fixture.whenStable();
    fixture.detectChanges();

    const comp = fixture.componentInstance;
    expect(comp.error).toContain('Kon post niet laden.');
    expect(comp.loading).toBeFalse();
  });

  it('save(): valide form → update + navigate', async () => {
    postSvc.getById.and.returnValue(Promise.resolve(post));
    postSvc.updateDraft.and.returnValue(Promise.resolve(post));
    const fixture = TestBed.createComponent(UpdateArticleComponent);
    fixture.detectChanges();
    await fixture.whenStable();

    const comp = fixture.componentInstance;
    comp.form.setValue({ title: 'Nieuw', description: 'Desc' });

    await comp.save();

    expect(postSvc.updateDraft).toHaveBeenCalledWith(5, 'Nieuw', 'Desc');
    expect(router.navigate).toHaveBeenCalledWith(['/admin/drafts']);
    expect(comp.saving).toBeFalse();
    expect(comp.error).toBe('');
  });

  it('save(): invalid form → geen update', async () => {
    postSvc.getById.and.returnValue(Promise.resolve(post));
    const fixture = TestBed.createComponent(UpdateArticleComponent);
    fixture.detectChanges();
    await fixture.whenStable();

    const comp = fixture.componentInstance;
    comp.form.setValue({ title: '', description: '' });

    await comp.save();

    expect(postSvc.updateDraft).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('save(): update error → toont fout', async () => {
    postSvc.getById.and.returnValue(Promise.resolve(post));
    postSvc.updateDraft.and.returnValue(Promise.reject('nope'));
    const fixture = TestBed.createComponent(UpdateArticleComponent);
    fixture.detectChanges();
    await fixture.whenStable();

    const comp = fixture.componentInstance;
    comp.form.setValue({ title: 'X', description: 'Y' });

    await comp.save();

    expect(comp.error).toContain('Opslaan mislukt.');
    expect(comp.saving).toBeFalse();
  });
});
