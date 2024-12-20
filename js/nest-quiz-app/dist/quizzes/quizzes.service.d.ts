import { Quiz } from './quiz.entity';
import { Repository } from 'typeorm';
import { CreateQuizInput } from './dto/create-quiz.input';
import { Question } from '../questions/questions.entity';
import { Answer } from '../answers/answer.entity';
import { ShowQuizDTO } from './dto/show-quiz.type';
import { ReturnQuizDTO } from './dto/return-quiz.input';
import { ResultDTO } from './dto/reusult.type';
export declare class QuizzesService {
    private quizzesRepository;
    private questionsRepository;
    private answersRepository;
    constructor(quizzesRepository: Repository<Quiz>, questionsRepository: Repository<Question>, answersRepository: Repository<Answer>);
    createQuiz(createQuizInput: CreateQuizInput): Promise<Quiz>;
    private checkAnswerOrder;
    findAll(): Promise<Quiz[]>;
    findOne(quizId: number): Promise<Quiz>;
    findOneToShow(quizId: number): Promise<ShowQuizDTO>;
    checkQuiz(returnedQuiz: ReturnQuizDTO): Promise<ResultDTO>;
}
