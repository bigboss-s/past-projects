import { Test, TestingModule } from '@nestjs/testing';
import { QuizzesService } from './quizzes.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { Quiz } from './quiz.entity';
import { Question, QuestionType } from '../questions/questions.entity';
import { Answer } from '../answers/answer.entity';
import { BadRequestException } from '@nestjs/common';

describe('QuizzesService', () => {
  let service: QuizzesService;

  const mockQuizzesRepository = {
    create: jest.fn().mockImplementation(dto => dto),
    save: jest.fn().mockImplementation(quiz => Promise.resolve({ id: 1, ...quiz })),
    findOneOrFail: jest.fn().mockImplementation(id => ({
      id: 1,
      questions: [
        {
          id: 1,
          type: QuestionType.SINGLE_ANSWER,
          answers: [
            {
              id: 1,
              isCorrect: true,
            },
            {
              id: 2,
              isCorrect: false,
            }
          ]
        },
        {
          id: 2,
          type: QuestionType.OPEN_ANSWER,
          answers: [
            {
              id: 3,
              answerString: 'correct answer'
            }
          ],
        },
        {
          id: 3,
          type: QuestionType.ORDERED_ANSWER,
          answers: [
            {
              id: 4,
              order: 1,
            },
            {
              id: 5,
              order: 2
            }
          ]
        }
      ]
    }))
  }

  const mockQuestionsRepository = {
    create: jest.fn().mockImplementation(dto => dto),
    save: jest.fn().mockImplementation(question => Promise.resolve({ id: 1, ...question })),
  }

  const mockAnswersRepository = {
    create: jest.fn().mockImplementation(dto => dto),
    save: jest.fn().mockImplementation(answer => Promise.resolve({ id: 1, ...answer })),
  }

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        QuizzesService,
        {
          provide: getRepositoryToken(Quiz),
          useValue: mockQuizzesRepository,
        },
        {
          provide: getRepositoryToken(Question),
          useValue: mockQuestionsRepository
        },
        {
          provide: getRepositoryToken(Answer),
          useValue: mockAnswersRepository
        }
      ],
    }).compile();

    service = module.get<QuizzesService>(QuizzesService);
  });

  it('should create a new quiz', async () => {
    expect.assertions(1);
    const quiz = await service.createQuiz({
      name: "test quiz",
      description: "test description",
      questions: [
        {
          questionString: "question one",
          type: QuestionType.SINGLE_ANSWER,
          answers: [
            {
              answerString: "answer one",
              isCorrect: true,
            },
            {
              answerString: "answer two",
              isCorrect: false,
            }
          ]
        }
      ]
    });
    expect(quiz).toEqual({
      id: expect.any(Number),
      name: "test quiz",
      description: "test description",
      questions: [
        {
          questionString: "question one",
          type: QuestionType.SINGLE_ANSWER,
          answers: [
            {
              answerString: "answer one",
              isCorrect: true,
              order: undefined
            },
            {
              answerString: "answer two",
              isCorrect: false,
              order: undefined
            }
          ]
        }
      ]
    })
  });

  it('should throw a BadRequestException because answers don\'t meet necessary criteria', async () => {
    expect.assertions(1);
    await expect(service.createQuiz({
      name: "test quiz",
      description: "test description",
      questions: [
        {
          questionString: "question one",
          type: QuestionType.SINGLE_ANSWER,
          answers: [
            {
              answerString: "answer one",
              isCorrect: false,
            },
            {
              answerString: "answer two",
              isCorrect: false,
            }
          ]
        }
      ]
    })).rejects.toThrow(new BadRequestException('Single answer question must have exactly one correct answer.'));
  })

  it('should score 2/3', async () => {
    const result = await service.checkQuiz({
      id: 1,
      questions: [
        {
          id: 1,
          type: QuestionType.SINGLE_ANSWER,
          answers: [
            {
              id: 1,
              isCheckedTrue: true,
            },
            {
              id: 2,
              isCheckedTrue: true,
            }
          ]
        },
        {
          id: 2,
          type: QuestionType.OPEN_ANSWER,
          answers: [
            {
              id: 3,
              answerString: 'correct answer'
            }
          ]
        },
        {
          id: 3,
          type: QuestionType.ORDERED_ANSWER,
          answers: [
            {
              id: 4,
              checkedOrder: 1,
            },
            {
              id: 5,
              checkedOrder: 2
            }
          ]
        }
      ]
    })
    expect(result).toEqual({
      scoredPoints: 2,
      maxPoints: 3,
    })
  })
});
