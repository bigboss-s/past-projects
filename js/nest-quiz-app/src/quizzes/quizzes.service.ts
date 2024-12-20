import { BadRequestException, Injectable } from '@nestjs/common';
import { Quiz } from './quiz.entity';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { CreateQuizInput } from './dto/create-quiz.input';
import { Question, QuestionType } from '../questions/questions.entity';
import { Answer } from '../answers/answer.entity';
import { CreateAnswerInput } from '../answers/dto/create-answer.input';
import { ShowQuizDTO } from './dto/show-quiz.type';
import { ReturnQuizDTO } from './dto/return-quiz.input';
import { distance } from 'fastest-levenshtein';
import { ResultDTO } from './dto/reusult.type';

@Injectable()
export class QuizzesService {
  constructor(
    @InjectRepository(Quiz) private quizzesRepository: Repository<Quiz>,
    @InjectRepository(Question) private questionsRepository: Repository<Question>,
    @InjectRepository(Answer) private answersRepository: Repository<Answer>
  ) { }

  async createQuiz(createQuizInput: CreateQuizInput): Promise<Quiz> {
    createQuizInput.questions.forEach(question => {
      const correctAnswersCount = question.answers.filter(answer => answer.isCorrect).length;
      const correctNotNullAnswersCount = question.answers.filter(answer => answer.isCorrect !== null).length;
      const orderedAnswerCount = question.answers.filter(answer => answer.order !== null).length;
  
      if ([QuestionType.MULTIPLE_ANSWER, QuestionType.SINGLE_ANSWER].includes(question.type)
        && correctNotNullAnswersCount !== question.answers.length) {
        throw new BadRequestException("All answers to single and multiple answer questions must be marked as correct or false");
      }
      if (question.type === QuestionType.MULTIPLE_ANSWER && correctAnswersCount === 0) {
        throw new BadRequestException("Multiple answer questions must have at least one correct answer");
      }
      if (question.type === QuestionType.SINGLE_ANSWER && correctAnswersCount !== 1) {
        throw new BadRequestException("Single answer question must have exactly one correct answer.");
      }
      if (question.type === QuestionType.OPEN_ANSWER && question.answers.length !== 1) {
        throw new BadRequestException("Plain text answer questions must have exactly one answer");
      }
      if (question.type === QuestionType.ORDERED_ANSWER && orderedAnswerCount !== question.answers.length) {
        throw new BadRequestException("All answers to a sorting question must be ordered");
      }
      if (question.type === QuestionType.ORDERED_ANSWER && !this.checkAnswerOrder(question.answers)) {
        throw new BadRequestException("Incorrect answer order. Ordering should start on 1 and increment by 1.");
      }
    });
  
    const quiz = this.quizzesRepository.create({
      name: createQuizInput.name,
      description: createQuizInput.description,
      questions: createQuizInput.questions.map(question => ({
        questionString: question.questionString,
        type: question.type,
        answers: question.answers.map(answer => ({
          answerString: answer.answerString,
          isCorrect: answer.isCorrect,
          order: answer.order
        }))
      }))
    });
    return await this.quizzesRepository.save(quiz);
  }
  

  private checkAnswerOrder(answers: CreateAnswerInput[]) {
    const orders = answers.map(answer => answer.order);
    orders.sort();
    for (let i = 0; i < orders.length; i++) {
      if (orders[i] !== i + 1) {
        return false;
      }
    }
    return true;
  }

  async findAll(): Promise<Quiz[]> {
    return await this.quizzesRepository.find({
      relations: ['questions', 'questions.answers']
    });
  }

  async findOne(quizId: number): Promise<Quiz> {
    let quiz: Quiz;
    try {
      quiz = await this.quizzesRepository.findOneOrFail({
        where: { id: quizId },
        relations: ['questions', 'questions.answers'],
      });
    } catch (EntityNotFoundError){
      throw new BadRequestException('Quiz not found');
    }
    return quiz;
  }

  async findOneToShow(quizId: number): Promise<ShowQuizDTO> {
    const quiz = await this.findOne(quizId);
    const showQuiz = {
      id: quiz.id,
      name: quiz.name,
      description: quiz.description,
      questions: quiz.questions.map(question => {
        if (question.type !== QuestionType.OPEN_ANSWER) {
          return {
            id: question.id,
            questionString: question.questionString,
            type: question.type,
            answers: question.answers.map(answer => ({
              id: answer.id,
              answerString: answer.answerString
            })),
          }
        } else {
          return {
            id: question.id,
            questionString: question.questionString,
            type: question.type
          }
        }
      })
    }
    return showQuiz;
  }

  async checkQuiz(returnedQuiz: ReturnQuizDTO): Promise<ResultDTO> {
    let maxPoints = 0;
    let scoredPoints = 0;

    const quiz = await this.findOne(returnedQuiz.id);
    const questionMap = quiz.questions.reduce((acc, question) => {
      acc[question.id] = question.answers;
      return acc;
    }, {});
    for (const returnedQuestion of returnedQuiz.questions) {
      maxPoints++;
      switch (returnedQuestion.type) {
        case QuestionType.SINGLE_ANSWER:
        case QuestionType.MULTIPLE_ANSWER: {
          const answerMap: Record<number, boolean> = {};
          questionMap[returnedQuestion.id].forEach(question => {
            answerMap[question.id] = question.isCorrect;
          });
          let isCorrect = true;
          for (const returnedAnswer of returnedQuestion.answers) {
            if (returnedAnswer.isCheckedTrue !== answerMap[returnedAnswer.id]) {
              //console.log("Miss on " + returnedAnswer.id + ": " + returnedAnswer.isCheckedTrue + " instead of " + answerMap[returnedAnswer.id]);
              isCorrect = false;
              break;
            }
          }
          if (isCorrect) scoredPoints++;
          break;
        }

        case QuestionType.ORDERED_ANSWER: {
          const answerMap: Record<number, number> = {};
          questionMap[returnedQuestion.id].forEach(question => {
            answerMap[question.id] = question.order;
          })
          let isCorrect = true;
          for (const returnedAnswer of returnedQuestion.answers) {
            if (returnedAnswer.checkedOrder !== answerMap[returnedAnswer.id]) {
              isCorrect = false;
              //console.log("Miss on " + returnedAnswer.id + ": " + returnedAnswer.checkedOrder + " instead of " + answerMap[returnedAnswer.id])
              break;
            }
          }
          if (isCorrect) scoredPoints++;
          break;
        }

        case QuestionType.OPEN_ANSWER: {
          const correctAnswer = questionMap[returnedQuestion.id][0].answerString;
          if (distance(correctAnswer, returnedQuestion.answers[0].answerString) < 3) {
            scoredPoints++
          } else {
            //console.log("Miss on " + returnedQuestion.answers[0].id + ": " + returnedQuestion.answers[0].answerString + " instead of " + correctAnswer);
          }
          break;
        }
      }
    }
    return {
      scoredPoints: scoredPoints,
      maxPoints: maxPoints
    }

  }
}
