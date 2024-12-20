import { QuizzesService } from './quizzes.service';
import { Quiz } from './quiz.entity';
import { CreateQuizInput } from './dto/create-quiz.input';
import { ShowQuizDTO } from './dto/show-quiz.type';
import { ReturnQuizDTO } from './dto/return-quiz.input';
import { ResultDTO } from './dto/reusult.type';
export declare class QuizzesResolver {
    private quizzesService;
    constructor(quizzesService: QuizzesService);
    getQuizzes(): Promise<Quiz[]>;
    createQuiz(createQuizInput: CreateQuizInput): Promise<Quiz>;
    getFullQuiz(id: number): Promise<Quiz>;
    showQuiz(id: number): Promise<ShowQuizDTO>;
    checkQuiz(returnedQuiz: ReturnQuizDTO): Promise<ResultDTO>;
}
