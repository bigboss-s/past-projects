import { Query, Resolver } from '@nestjs/graphql';
import { Question } from './questions.entity';
import { QuestionsService } from './questions.service';

@Resolver(of => Question)
export class QuestionsResolver {
    constructor(private questionsService: QuestionsService) {}

    @Query(returns => [Question])
    async questions(): Promise<Question []> {
        return this.questionsService.findAll();
    }
}
