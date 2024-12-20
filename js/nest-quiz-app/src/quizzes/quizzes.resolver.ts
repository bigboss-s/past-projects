import { Resolver, Query, Mutation, Args, Int } from '@nestjs/graphql';
import { QuizzesService } from './quizzes.service';
import { Quiz } from './quiz.entity';
import { CreateQuizInput } from './dto/create-quiz.input';
import { ShowQuizDTO } from './dto/show-quiz.type';
import { ReturnQuizDTO } from './dto/return-quiz.input';
import { ResultDTO } from './dto/reusult.type';

@Resolver(of => Quiz)
export class QuizzesResolver {
    constructor(private quizzesService: QuizzesService) {}

    @Query(returns => [Quiz])
    async getQuizzes(): Promise<Quiz []> {
        return await this.quizzesService.findAll();
    }

    @Mutation(returns => Quiz)
    async createQuiz(@Args('CreateQuizInput') createQuizInput: CreateQuizInput): Promise<Quiz> {
        return await this.quizzesService.createQuiz(createQuizInput);
    }

    @Query(returns => Quiz)
    async getFullQuiz(@Args('id', {type: () => Int}) id: number): Promise<Quiz> {
        return await this.quizzesService.findOne(id);
    }

    @Query(returns => ShowQuizDTO)
    async showQuiz(@Args('id', {type: () => Int}) id: number): Promise<ShowQuizDTO> {
        return await this.quizzesService.findOneToShow(id);
    }

    @Query(returns => ResultDTO)
    async checkQuiz(@Args('ReturnQuizDTO', {type: () => ReturnQuizDTO}) returnedQuiz: ReturnQuizDTO): Promise<ResultDTO>{
        return await this.quizzesService.checkQuiz(returnedQuiz);

    }
}
