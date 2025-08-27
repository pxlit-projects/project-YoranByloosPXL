import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DraftListComponent } from './draft-list.component';
import { Post } from '../../../models/post.model';
import { PostService } from '../../../services/post/post.service';
import { Router } from '@angular/router';

describe('DraftListComponent', () => {
  let postsApi: jasmine.SpyObj<PostService>;
  let router: jasmine.SpyObj<Router>;

  const DRAFTS: Post[] = [
    {
      id: 1, title: 'A', content: 'a', author: 'ann',
      status: 'CONCEPT', published: false,
      createdAt: '2024-01-01T00:00:00', updatedAt: '2024-01-01T00:00:00'
    },
    {
      id: 2, title: 'B', content: 'b', author: 'bob',
      status: 'CONCEPT', published: false,
      createdAt: '2024-01-02T00:00:00', updatedAt: '2024-01-02T00:00:00'
    },
  ];

  beforeEach(async () => {
    postsApi = jasmine.createSpyObj<PostService>('PostService', [
      'getDrafts', 'submitDraft'
    ]);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [DraftListComponent],
      providers: [
        { provide: PostService, useValue: postsApi },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();
  });

  function create() {
    const fixture = TestBed.createComponent(DraftListComponent);
    const comp = fixture.componentInstance;
    return { fixture, comp };
  }

  it('laadt drafts in ngOnInit en toont kaarten', async () => {
    postsApi.getDrafts.and.returnValue(Promise.resolve([...DRAFTS]));

    const { fixture, comp } = create();
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(postsApi.getDrafts).toHaveBeenCalled();
    expect(comp.loading).toBeFalse();
    expect(comp.error).toBe('');
    expect(comp.drafts.length).toBe(2);

    const cards = fixture.debugElement.queryAll(By.css('app-draft-card'));
    expect(cards.length).toBe(2);
  });

  it('handlePublish: dient in en verwijdert uit lijst', async () => {
    postsApi.submitDraft.and.returnValue(Promise.resolve(DRAFTS[0]));

    const { comp } = create();
    comp.drafts = [...DRAFTS];

    await comp.handlePublish(DRAFTS[0]);

    expect(postsApi.submitDraft).toHaveBeenCalledOnceWith(1);
    expect(comp.drafts.map(d => d.id)).toEqual([2]);
  });

  it('handlePublish: toont alert bij fout', async () => {
    postsApi.submitDraft.and.returnValue(Promise.reject(new Error('boom')));
    const alertSpy = spyOn(window, 'alert');

    const { comp } = create();
    comp.drafts = [...DRAFTS];

    await comp.handlePublish(DRAFTS[0]);

    expect(alertSpy).toHaveBeenCalled();
    expect(comp.drafts.length).toBe(2);
  });

  it('handleEdit: navigeert naar update met state', () => {
    const { comp } = create();
    comp.handleEdit(DRAFTS[1]);

    expect(router.navigate).toHaveBeenCalledOnceWith(
      ['/admin/update', 2],
      { state: { post: DRAFTS[1] } }
    );
  });

  it('newArticle: navigeert naar write', () => {
    const { comp } = create();
    comp.newArticle();

    expect(router.navigate).toHaveBeenCalledOnceWith(['/admin/write']);
  });
});
